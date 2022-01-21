"use strict";

const gulp = require('gulp');
const boilerplate = require('@appium/gulp-plugins').boilerplate.use(gulp);

boilerplate({
  build: 'android-apidemos',
  files: ['index.js', '!gulpfile.js'],
});
