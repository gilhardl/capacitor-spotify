import { PluginListenerHandle } from "@capacitor/core";

declare module "@capacitor/core" {
  interface PluginRegistry {
    SpotifySDK: SpotifySDKPlugin;
  }
}

export type CrossfadeState = {
  duration: number;
  isEnabled: boolean;
};

export type PlayerState = {
  isPaused: boolean;
  playbackOptions: PlayerOptions;
  playbackPosition: number;
  playbackRestrictions: PlayerRestrictions;
  playbackSpeed: number;
  track: Track;
};

export type PlayerOptions = {
  isShuffling: boolean;
  repeatMode: number;
};

export type PlayerRestrictions = {
  canRepeatContext: boolean;
  canRepeatTrack: boolean;
  canSeek: boolean;
  canSkipNext: boolean;
  canSkipPrev: boolean;
  canToggleShuffle: boolean;
};

export type PlayerContext = {
  subtitle: string;
  title: string;
  type: string;
  uri: string;
};

export type Track = {
  album: Album;
  artist: Artist;
  artists: Artist[];
  duration: number;
  imageUri: ImageUri;
  isEpisode: boolean;
  isPodcast: boolean;
  name: string;
  uri: string;
};

export type Album = {
  name: string;
  uri: string;
};

export type Artist = {
  name: string;
  uri: string;
};

export type ImageUri = {
  raw: string;
};

export interface SpotifySDKPlugin {
  initialize(options: {
    clientId: string;
    redirectUri: string;
  }): Promise<{ result: boolean }>;

  login(): Promise<{ result: boolean }>;

  logout(): Promise<{ result: boolean }>;

  connectToAppRemote(): Promise<{ result: boolean }>;

  disconnectFromAppRemote(): Promise<{ result: boolean }>;

  getCrossfadeState(): Promise<{ crossfadeState: CrossfadeState }>;

  getPlayerState(): Promise<{ playerState: PlayerState }>;

  subscribeToPlayerState(): void;
  addListener(
    eventName: "playerState",
    listenerFunc: (state: PlayerState) => void
  ): PluginListenerHandle;

  subscribeToPlayerContext(): void;
  addListener(
    eventName: "playerContext",
    listenerFunc: (context: PlayerContext) => void
  ): PluginListenerHandle;

  play(options: { uri: string }): Promise<{ result: boolean }>;

  pause(): Promise<{ result: boolean }>;

  resume(): Promise<{ result: boolean }>;

  queue(options: { uri: string }): Promise<{ result: boolean }>;

  skipPrevious(): Promise<{ result: boolean }>;

  skipNext(): Promise<{ result: boolean }>;

  skipToIndex(options: {
    uri: string;
    index: number;
  }): Promise<{ result: boolean }>;

  seekTo(options: { milliseconds: number }): Promise<{ result: boolean }>;

  seekToRelativePosition(options: {
    milliseconds: number;
  }): Promise<{ result: boolean }>;

  setRepeat(options: { repeatMode: number }): Promise<{ result: boolean }>;

  setShuffle(options: { enabled: boolean }): Promise<{ result: boolean }>;

  toggleRepeat(): Promise<{ result: boolean }>;

  toggleShuffle(): Promise<{ result: boolean }>;
}
