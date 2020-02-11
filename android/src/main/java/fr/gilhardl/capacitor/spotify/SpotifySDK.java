package fr.gilhardl.capacitor.spotify;

import android.content.Intent;
import android.util.Log;

import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.CrossfadeState;
import com.spotify.protocol.types.PlayerContext;
import com.spotify.protocol.types.PlayerState;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

@NativePlugin(
    requestCodes={SpotifySDK.LOGIN_REQUEST_CODE}
)
public class SpotifySDK extends Plugin {
    private static final String TAG = "Capacitor/SpotifySDK";

    protected static String CLIENT_ID;
    protected static String REDIRECT_URI;
    protected static final int LOGIN_REQUEST_CODE = 12345;

    private SpotifyAppRemote mSpotifyAppRemote;

    @PluginMethod()
    public void initialize(PluginCall call) {
        String clientId = call.getString("clientId");
        String redirectUri = call.getString("redirectUri");

        if (clientId == null || redirectUri == null) {
            call.reject("Client ID or redirect URI missing");
            return;
        }

        CLIENT_ID = clientId;
        REDIRECT_URI = redirectUri;

        JSObject result = new JSObject();
        result.put("result", true);
        call.resolve(result);
    }

    @PluginMethod()
    public void login(final PluginCall call) {
        saveCall(call);

        AuthorizationRequest.Builder builder =
                new AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI);

        builder.setScopes(new String[]{"streaming"});
        AuthorizationRequest request = builder.build();

        Intent intent = AuthorizationClient.createLoginActivityIntent(getActivity(), request);
        startActivityForResult(call, intent, LOGIN_REQUEST_CODE);
    }

    @Override
    protected void handleOnActivityResult(int requestCode, int resultCode, Intent intent) {
        super.handleOnActivityResult(requestCode, resultCode, intent);
        PluginCall savedCall = getSavedCall();
        if (savedCall == null) {
            Log.i(TAG, "No capacitor saved call. Aborting result processing...");
            return;
        }

        // Check if result comes from the correct activity
        if (requestCode == LOGIN_REQUEST_CODE) {
            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, intent);

            JSObject result = new JSObject();

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    // Handle successful response
                    result.put("result", true);
                    result.put("accessToken", response.getAccessToken());
                    savedCall.resolve(result);
                    break;

                // Auth flow returned an error
                case ERROR:
                    // Handle error response
                    savedCall.reject(response.getError());
                    break;

                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
                    result.put("result", false);
                    savedCall.resolve(result);
            }
        }
    }

    @PluginMethod()
    public void logout(final PluginCall call) {
        AuthorizationClient.clearCookies(getContext());

        JSObject result = new JSObject();
        result.put("result", true);
        call.resolve(result);
    }

    @PluginMethod()
    public void connectToAppRemote(final PluginCall call) {
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        Connector.ConnectionListener listener = new Connector.ConnectionListener() {
            @Override
            public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                mSpotifyAppRemote = spotifyAppRemote;

                JSObject result = new JSObject();
                result.put("result", true);
                call.resolve(result);
            }

            @Override
            public void onFailure(Throwable throwable) {
                call.reject(throwable.getMessage());
            }
        };

        SpotifyAppRemote.connect(getContext(), connectionParams, listener);
    }

    @PluginMethod()
    public void disconnectFromAppRemote(final PluginCall call) {
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
        JSObject result = new JSObject();
        result.put("result", true);
        call.resolve(result);
    }

    @PluginMethod()
    public void getCrossfadeState(final PluginCall call) {
        mSpotifyAppRemote.getPlayerApi()
                .getCrossfadeState()
                .setResultCallback(new CallResult.ResultCallback<CrossfadeState>() {
                    @Override
                    public void onResult(CrossfadeState crossfadeState) {
                        JSObject result = new JSObject();
                        result.put("crossfadeState", crossfadeState);
                        call.resolve(result);
                    }
                });
    }

    @PluginMethod()
    public void getPlayerState(final PluginCall call) {
        mSpotifyAppRemote.getPlayerApi()
                .getPlayerState()
                .setResultCallback(new CallResult.ResultCallback<PlayerState>() {
                    @Override
                    public void onResult(PlayerState playerState) {
                        JSObject result = new JSObject();
                        result.put("playerState", playerState);
                        call.resolve(result);
                    }
                });
    }

    @PluginMethod()
    public void subscribeToPlayerState(final PluginCall call) {
        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(new Subscription.EventCallback<PlayerState>() {
                    @Override
                    public void onEvent(PlayerState playerState) {
                        JSObject event = new JSObject();
                        event.put("state", playerState);
                        notifyListeners("playerState", event);
                    }
                });
    }

    @PluginMethod()
    public void subscribeToPlayerContext(final PluginCall call) {
        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerContext()
                .setEventCallback(new Subscription.EventCallback<PlayerContext>() {
                    @Override
                    public void onEvent(PlayerContext playerContext) {
                        JSObject event = new JSObject();
                        event.put("context", playerContext);
                        notifyListeners("playerContext", event);
                    }
                });
    }

    @PluginMethod()
    public void play(final PluginCall call) {
        String spotifyUri = call.getString("uri");

        if (spotifyUri == null) {
            call.reject("Spotify URI missing");
            return;
        }

        mSpotifyAppRemote.getPlayerApi().play(spotifyUri);

        JSObject result = new JSObject();
        result.put("result", true);
        call.resolve(result);
    }

    @PluginMethod()
    public void pause(final PluginCall call) {
        mSpotifyAppRemote.getPlayerApi().pause();

        JSObject result = new JSObject();
        result.put("result", true);
        call.resolve(result);
    }

    @PluginMethod()
    public void resume(final PluginCall call) {
        mSpotifyAppRemote.getPlayerApi().resume();

        JSObject result = new JSObject();
        result.put("result", true);
        call.resolve(result);
    }

    @PluginMethod()
    public void queue(final PluginCall call) {
        String spotifyUri = call.getString("uri");

        if (spotifyUri == null) {
            call.reject("Spotify URI missing");
            return;
        }

        mSpotifyAppRemote.getPlayerApi().queue(spotifyUri);

        JSObject result = new JSObject();
        result.put("result", true);
        call.resolve(result);
    }

    @PluginMethod()
    public void skipPrevious(final PluginCall call) {
        mSpotifyAppRemote.getPlayerApi().skipPrevious();

        JSObject result = new JSObject();
        result.put("result", true);
        call.resolve(result);
    }

    @PluginMethod()
    public void skipNext(final PluginCall call) {
        mSpotifyAppRemote.getPlayerApi().skipNext();

        JSObject result = new JSObject();
        result.put("result", true);
        call.resolve(result);
    }

    @PluginMethod()
    public void skipToIndex(final PluginCall call) {
        String spotifyUri = call.getString("uri");
        Integer index = call.getInt("index");

        if (spotifyUri == null || index == null) {
            call.reject("Spotify URI or index missing");
            return;
        }

        mSpotifyAppRemote.getPlayerApi().skipToIndex(spotifyUri, index);

        JSObject result = new JSObject();
        result.put("result", true);
        call.resolve(result);
    }

    @PluginMethod()
    public void seekTo(final PluginCall call) {
        Float ms = call.getFloat("milliseconds");

        if (ms == null) {
            call.reject("Milliseconds missing");
            return;
        }

        mSpotifyAppRemote.getPlayerApi().seekTo(ms.longValue());

        JSObject result = new JSObject();
        result.put("result", true);
        call.resolve(result);
    }

    @PluginMethod()
    public void seekToRelativePosition(final PluginCall call) {
        Float ms = call.getFloat("milliseconds");

        if (ms == null) {
            call.reject("Milliseconds missing");
            return;
        }

        mSpotifyAppRemote.getPlayerApi().seekToRelativePosition(ms.longValue());

        JSObject result = new JSObject();
        result.put("result", true);
        call.resolve(result);
    }

    @PluginMethod()
    public void setRepeat(final PluginCall call) {
        Integer repeatMode = call.getInt("repeatMode");

        if (repeatMode == null) {
            call.reject("Repeat mode missing");
            return;
        }

        mSpotifyAppRemote.getPlayerApi().setRepeat(repeatMode);

        JSObject result = new JSObject();
        result.put("result", true);
        call.resolve(result);
    }

    @PluginMethod()
    public void setShuffle(final PluginCall call) {
        Boolean enabled = call.getBoolean("enabled");

        if (enabled == null) {
            call.reject("Enabled missing");
            return;
        }

        mSpotifyAppRemote.getPlayerApi().setShuffle(enabled);

        JSObject result = new JSObject();
        result.put("result", true);
        call.resolve(result);
    }

    @PluginMethod()
    public void toggleRepeat(final PluginCall call) {
        mSpotifyAppRemote.getPlayerApi().toggleRepeat();

        JSObject result = new JSObject();
        result.put("result", true);
        call.resolve(result);
    }

    @PluginMethod()
    public void toggleShuffle(final PluginCall call) {
        mSpotifyAppRemote.getPlayerApi().toggleShuffle();

        JSObject result = new JSObject();
        result.put("result", true);
        call.resolve(result);
    }
}
