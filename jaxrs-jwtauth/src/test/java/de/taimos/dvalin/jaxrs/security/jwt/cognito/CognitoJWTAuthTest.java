package de.taimos.dvalin.jaxrs.security.jwt.cognito;

import java.text.ParseException;

import de.taimos.dvalin.test.inject.InjectionUtils;

public class CognitoJWTAuthTest {
    
    public static void main(String[] args) throws ParseException {
        String jwt = "PasteTokenHere";
        
        CognitoJWTAuth auth = new CognitoJWTAuth();
        InjectionUtils.injectValue(auth, "cognitoPoolId", "POOL_ID_HERE");
        InjectionUtils.injectValue(auth, "cognitoPoolRegion", "eu-central-1");
        InjectionUtils.injectValue(auth, "cognitoRoles", "cognito:groups");
        
        auth.init();
        
        CognitoUser user = auth.validateToken(jwt);
        System.out.println(user.toString());
    }
}
