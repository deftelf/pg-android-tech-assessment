# Thoughts on things still to do

This is what I would do next before making this a real PR into a real codebase:

## Improve the swipe to dismiss
This feels a bit 'snappy', it does not animate all the way off and I'm not sure why as it is inline with the examples. Might be something to do with not adding the background composable

## Improve the list animations
For some reason the standard LazyColumn animations only animate item movement and not item add / remove. I believe this has to be done manually.

## Tidyup
- Move the compose functions to a separate file
- Extract the strings
- Move the gradle dependency versions to a toml file so we can reference them and keep them aligned
- Probably need to set up some OKHTTP timeout rules as I'm not sure I would want the defaults

## Tests
Setting up a proper test harness is quite timeconsuming so I haven't done it here but this is what I would aim to do:
- Convert our repository to an interface and set up of FakeFactRepository that we can inject using a @TestInstallIn module, that we can control the response in a test build
- End-to-end test the screen using this fake repository and all the responses that it can give using ComposeTestRule. We can then see that the correct items / loading / state is being shown to the user
- Move list management logic to its own usecase and test it specifically using unit tests to check the FIFO / 3 limit behaviour