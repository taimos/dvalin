## JSON Web Tokens

For Web Token support include the additional dependency `dvalin-jaxrs-jwtauth`.
 
### Shared secret

To use shared secret based JWT set the following properties:

* `jwtauth.issuer` - the issuer of the tokens
* `jwtauth.secret` - the shared secret to sign web tokens with
* `jwtauth.timeout` - optional timeout of the tokens (defaults to one hour)

You can then create WebTokens using the `JWTAuth` bean and they are automatically validated when set as Bearer type Authorization.

### AWS Cognito

You can also use AWS Cognito user pools as JWT source for `id` and `access` tokens. 

Specify the following properties:

* `jwtauth.cognito.poolid` - the Cognito user pool id
* `jwtauth.cognito.region` - the Cognito user pool region
* `jwtauth.cognito.roles` - the field to use as roles; defaults to `cognito:groups`

The security context then contains a `de.taimos.dvalin.jaxrs.security.jwt.cognito.CognitoUser` with all fields.
