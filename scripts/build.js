const fs = require('fs');
const tp = require('teen_process');
const path = require('path');

const APKS_DIR = path.resolve('.', 'apks');
const APK_FILENAME = 'ApiDemos-debug.apk';
const APK_ORIGIN_PATH = path.resolve('.', 'app', 'build', 'outputs', 'apk', 'debug', 'app-debug.apk');
const APK_DEST_PATH = path.join(APKS_DIR, APK_FILENAME);

async function runGradleBuild() {
  try {
    if (process.platform === 'win32') {
      await tp.exec('cmd.exe', ['/c', 'gradlew.bat', 'clean', 'assembleDebug']);
    } else {
      await tp.exec('sh', ['gradlew', 'clean', 'assembleDebug']);
    }
  } catch (e) {
    throw new Error(`Cannot perform 'clean assembleDebug' Gradle tasks: ${e.stderr || e.message}`);
  }
}

async function moveAPK() {
  try {
    await fs.promises.access(APKS_DIR, fs.constants.R_OK);
  } catch (e) {
    await fs.promises.mkdir(APKS_DIR, { recursive: true });
  }
  await fs.promises.rename(APK_ORIGIN_PATH, APK_DEST_PATH);
}

async function main() {
  await runGradleBuild();
  await moveAPK();
}

(async () => await main())();
