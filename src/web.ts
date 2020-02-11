import { WebPlugin } from "@capacitor/core";
import { SpotifySDKPlugin, CrossfadeState, PlayerState } from "./definitions";

const errorMessage = "Spotify SDK isn't supported on the web";

export class SpotifySDKWeb extends WebPlugin implements SpotifySDKPlugin {
  constructor() {
    super({
      name: "SpotifySDK",
      platforms: ["web"]
    });
  }

  async initialize(options: {
    clientId: string;
    redirectUri: string;
  }): Promise<{ result: boolean }> {
    console.error(errorMessage);
    throw errorMessage;
  }

  async login(): Promise<{ result: boolean }> {
    console.error(errorMessage);
    throw errorMessage;
  }

  async logout(): Promise<{ result: boolean }> {
    console.error(errorMessage);
    throw errorMessage;
  }

  async connectToAppRemote(): Promise<{ result: boolean }> {
    console.error(errorMessage);
    throw errorMessage;
  }

  async disconnectFromAppRemote(): Promise<{ result: boolean }> {
    console.error(errorMessage);
    throw errorMessage;
  }

  async getCrossfadeState(): Promise<{
    crossfadeState: CrossfadeState;
  }> {
    console.error(errorMessage);
    throw errorMessage;
  }

  async getPlayerState(): Promise<{ playerState: PlayerState }> {
    console.error(errorMessage);
    throw errorMessage;
  }

  subscribeToPlayerState(): void {
    console.error(errorMessage);
    throw errorMessage;
  }

  subscribeToPlayerContext(): void {
    console.error(errorMessage);
    throw errorMessage;
  }

  async play(options: { uri: string }): Promise<{ result: boolean }> {
    console.error(errorMessage);
    throw errorMessage;
  }

  async pause(): Promise<{ result: boolean }> {
    console.error(errorMessage);
    throw errorMessage;
  }

  async resume(): Promise<{ result: boolean }> {
    console.error(errorMessage);
    throw errorMessage;
  }

  async queue(options: { uri: string }): Promise<{ result: boolean }> {
    console.error(errorMessage);
    throw errorMessage;
  }

  async skipPrevious(): Promise<{ result: boolean }> {
    console.error(errorMessage);
    throw errorMessage;
  }

  async skipNext(): Promise<{ result: boolean }> {
    console.error(errorMessage);
    throw errorMessage;
  }

  async skipToIndex(options: {
    uri: string;
    index: number;
  }): Promise<{ result: boolean }> {
    console.error(errorMessage);
    throw errorMessage;
  }

  async seekTo(options: {
    milliseconds: number;
  }): Promise<{ result: boolean }> {
    console.error(errorMessage);
    throw errorMessage;
  }

  async seekToRelativePosition(options: {
    milliseconds: number;
  }): Promise<{ result: boolean }> {
    console.error(errorMessage);
    throw errorMessage;
  }

  async setRepeat(options: {
    repeatMode: number;
  }): Promise<{ result: boolean }> {
    console.error(errorMessage);
    throw errorMessage;
  }

  async setShuffle(options: {
    enabled: boolean;
  }): Promise<{ result: boolean }> {
    console.error(errorMessage);
    throw errorMessage;
  }

  async toggleRepeat(): Promise<{ result: boolean }> {
    console.error(errorMessage);
    throw errorMessage;
  }

  async toggleShuffle(): Promise<{ result: boolean }> {
    console.error(errorMessage);
    throw errorMessage;
  }
}

const SpotifySDK = new SpotifySDKWeb();

export { SpotifySDK };

import { registerWebPlugin } from "@capacitor/core";
registerWebPlugin(SpotifySDK);
