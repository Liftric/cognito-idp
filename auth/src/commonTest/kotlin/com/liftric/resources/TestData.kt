package com.liftric.resources

val signInResponse = "{\n" +
        "    \"AuthenticationResult\": {\n" +
        "        \"AccessToken\": \"ABCDEDFG\",\n" +
        "        \"ExpiresIn\": 3600,\n" +
        "        \"IdToken\": \"HIJKLMNOP\",\n" +
        "        \"RefreshToken\": \"QRSTUVWXYZ\",\n" +
        "        \"TokenType\": \"Bearer\"\n" +
        "    },\n" +
        "    \"ChallengeParameters\": {}\n" +
        "}"

val signInErrorResponse = "{\"__type\": \"InvalidParameterException\", \"message\": \"Encountered an error\"}"