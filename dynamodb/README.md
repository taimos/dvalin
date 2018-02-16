## dynamodb

The dynamodb library adds support for the AWS DynamoDB data store. By including the AWS cloud module you 
get the full support to interact with DynamoDB using the SDK or the DynamoDBMapper.

### Abstract DAO implementation

The library provides a general purpose DAO implementation (`AbstractDynamoDAO`) with automatic initialization
and table creation. If you set the `dynamodb.url` property the endpoint of the SDK is reconfigured. This 
enables the use of the local DynamoDB version for development.
