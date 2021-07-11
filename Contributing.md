# Contributing

Auth is a simple kotlin project with one caveat: We're using a live Cogntio Userpool for integration tests and
the values are provided using code generation at compile time.

The build needs both `region` and `clientid` configured, either using our hashicorp vault cluster (obviously not accessible from the outside),
or via env var (github actions approach). `region` expects the AWS Region Code for the target region, like "us-east-1".

So if you only want to build the project, provide `region` and `clientid` env var with garbage values...

... and if you want to execute to tests yourself, you can use your own congito user pool client values.
