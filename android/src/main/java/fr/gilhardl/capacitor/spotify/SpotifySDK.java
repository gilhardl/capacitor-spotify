package fr.gilhardl.capacitor.spotify;

import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.types.CrossfadeState;
import com.spotify.protocol.types.PlayerContext;
import com.spotify.protocol.types.PlayerState;

@NativePlugin()
public class SpotifySDK extends Plugin {
    private static String CLIENT_ID;
    private static String REDIRECT_URI;
    private SpotifyAppRemote mSpotifyAppRemote;

    @PluginMethod()
    public void initializeAppRemote(PluginCall call) {
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
        call.success(result);
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
