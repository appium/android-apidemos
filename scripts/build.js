const tp = require('teen_process');

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

async function main() {
  await runGradleBuild();
}

(async () => await main())();
