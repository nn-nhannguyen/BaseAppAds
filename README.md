### MVVM:

- Kotlin
- Dagger Hilt
- Coroutine
- Room
- Retrofit
- Admob

### ■ [ktlint](https://github.com/pinterest/ktlint)

Once you executed `./gradlew addKtlintFormatGitPreCommitHook` , it enables to
run `./gradlew ktlintFormat` on each git commit.
Basically, standard and experimental rules are applied on ktlintFormat. If you want to add extra
rules, you can edit `ktlint.gradle` or `.editorconfig` .

#### ■ [BuildType](https://developer.android.com/studio/build/build-variants)

We have `debug` and `release` as specified on [app/build.gradle]
For more detail: https://developer.android.com/studio/build/build-variants

#### ■ [Fastlane](https://fastlane.tools/)
* Official website: https://fastlane.tools/
* Doc: https://docs.fastlane.tools/getting-started/android/setup/

#### Add ci/cd
- config gitlab-ci.yml
- add github actions on `.github/workflows/ci.yml`
