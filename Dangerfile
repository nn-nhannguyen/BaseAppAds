gitlab.dismiss_out_of_range_messages

# Ignore inline messages which lay outside a diff's range of PR
gitlab.dismiss_out_of_range_messages

# Make it more obvious that a PR is a work in progress and shouldn't be merged yet
#warn("PR is classed as Work in Progress") if gitlab.mr_json["draft"]
warn("MR is classed as Work in Progress") if gitlab.mr_title.include? "[WIP]"
#failure "Please provide a summary in the Merge Request description" if gitlab.mr_body.length < 5
warn("MR is classed as Work in Progress") if gitlab.mr_title.include? "[Draft]"
# Warn when there is a big PR
warn("Big PR") if git.lines_of_code > 500
android_lint.skip_gradle_task = true
lint_result_file = "app/build/reports/lint-results-devDebug.xml"
# Then, use it in your Danger rules or checks
if File.exist?(lint_result_file)
  android_lint.report_file = "app/build/reports/lint-results-devDebug.xml"
else
  warn("Lint result file not found: #{lint_result_file}")
end
android_lint.lint
checkstyle_format.base_path = Dir.pwd

ktlintPathCheck = 'app/build/reports/ktlint/ktlintMainSourceSetCheck/ktlintMainSourceSetCheck.xml'

if File.exist?(ktlintPathCheck)
  checkstyle_format.report 'app/build/reports/ktlint/ktlintMainSourceSetCheck/ktlintMainSourceSetCheck.xml'
else
  warn("Lint result file not found: #{ktlintPathCheck}")
end
