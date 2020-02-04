declare module "@capacitor/core" {
  interface PluginRegistry {
    SpotifySDK: SpotifySDKPlugin;
  }
}

export interface SpotifySDKPlugin {
  echo(options: { value: string }): Promise<{value: string}>;
}
