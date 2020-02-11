import Foundation
import Capacitor

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitor.ionicframework.com/docs/plugins/ios
 */
@objc(SpotifySDK)
public class SpotifySDK: CAPPlugin {
    internal static var CLIENT_ID: String = ""
    internal static var REDIRECT_URI: String = ""
    
    @objc func initialize(_ call: CAPPluginCall) {
        let clientId = call.getString("clientId") ?? ""
        let redirectUri = call.getString("redirectUri") ?? ""

        if (clientId == "" || redirectUri == "") {
            call.reject("Client ID or redirect URI missing");
            return;
        }

        SpotifySDK.CLIENT_ID = clientId
        SpotifySDK.REDIRECT_URI = redirectUri

        call.resolve(["result": true]);
    }
}
