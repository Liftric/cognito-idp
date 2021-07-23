# Migrating

Version 2.0.0 introduced a library rename and breaking changes:

| Subject      | Before | After     |
| :---        |  :---  |     :--- |
| Repository      | `com.liftric:auth:<version>` | `com.liftric:cognito-idp:<version>`   |
| Package   | `com.liftric.auth.*`        | `com.liftric.cognito.idp.*` |
| Client   | `AuthHandler`        | `IdentityProviderClient` |
| Client JS   | `AuthHandlerJS`        | `IdentityProviderClientJS` |
| Exceptions   | `NotAuthorizedException`        | `IdentityProviderException.NotAuthorized` |

Besides, also this:

- The configuration object is no longer needed. You provide the values directly to the `IdentityProviderClient` constructor.
- The `Region` enum has been removed, you now have to pass in the [string value](https://docs.aws.amazon.com/de_de/AWSEC2/latest/UserGuide/using-regions-availability-zones.html).
- To retrieve custom attributes from the tokens you now have to omit the `custom:` prefix.