import semanticReleaseConfig from '@appium/semantic-release-config';

export default semanticReleaseConfig({
  flavor: 'app',
  branches: ['master'],
  extraGitAssets: ['app/build.gradle'],
});
