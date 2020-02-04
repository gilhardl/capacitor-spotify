import { WebPlugin } from '@capacitor/core';
import { SpotifySDKPlugin } from './definitions';

export class SpotifySDKWeb extends WebPlugin implements SpotifySDKPlugin {
  constructor() {
    super({
      name: 'SpotifySDK',
      platforms: ['web']
    });
  }
}

const SpotifySDK = new SpotifySDKWeb();

export { SpotifySDK };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(SpotifySDK);
