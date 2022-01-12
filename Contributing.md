# Contributing

We're using a live Cognito user pool for integration tests and the values are provided using code generation at compile time.

The build needs both `region` and `clientId` environment variables defined. `region` expects the AWS region code for the target region, like "us-east-1".

If you only want to build the project, provide `region` and `clientId` environment variables with garbage values.

To execute the tests you have to use your own Cognito user pool client values.

## User Pool Setup

- When creating the user group make sure user account recovery was set to "Email if available, otherwise SMS". This is to ensure that `email` would not become required since the test suite doesn't use emails.
- Add `target_group` as custom attribute
- Add a Pre Signup Lambda that would automatically confirm the user:

```
exports.handler = (event, context, callback) => {
    event.response.autoConfirmUser = true;
    context.done(null, event);
};
```
