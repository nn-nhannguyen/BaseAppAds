source "https://rubygems.org"

gem "fastlane"
gem "danger"
gem 'danger-gitlab'
gem "danger-checkstyle_format"
gem "danger-android_lint"
# gem 'danger-ktlint'

plugins_path = File.join(File.dirname(__FILE__), 'fastlane', 'Pluginfile')
eval_gemfile(plugins_path) if File.exist?(plugins_path)
