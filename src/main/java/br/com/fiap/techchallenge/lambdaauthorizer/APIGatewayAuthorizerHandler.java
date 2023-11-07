package br.com.fiap.techchallenge.lambdaauthorizer;

import br.com.fiap.techchallenge.lambdaauthorizer.io.AuthPolicy;
import br.com.fiap.techchallenge.lambdaauthorizer.io.TokenAuthorizerContext;
import br.com.fiap.techchallenge.lambdaauthorizer.models.UserRole;
import br.com.fiap.techchallenge.lambdaauthorizer.services.AuthClient;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class APIGatewayAuthorizerHandler implements RequestHandler<TokenAuthorizerContext, AuthPolicy> {
    @Override
    public AuthPolicy handleRequest(TokenAuthorizerContext input, Context context) {

        String token = input.getAuthorizationToken();

        String methodArn = input.getMethodArn();
        String[] arnPartials = methodArn.split(":");
        String region = arnPartials[3];
        String awsAccountId = arnPartials[4];
        String[] apiGatewayArnPartials = arnPartials[5].split("/");
        String restApiId = apiGatewayArnPartials[0];
        String stage = apiGatewayArnPartials[1];
        AuthPolicy.HttpMethod httpMethod = AuthPolicy.HttpMethod.valueOf(apiGatewayArnPartials[2]);

        String resource = ""; // root resource
        if (apiGatewayArnPartials.length == 4) {
            resource = apiGatewayArnPartials[3];
        }

        var authClient = new AuthClient();
        var user = authClient.getUserInfo(token);

        String principalId = user.getId();

        if (user.getRoles().contains(UserRole.GUEST) || user.getRoles().contains(UserRole.CUSTOMER)) {
            return new AuthPolicy(principalId, AuthPolicy.PolicyDocument.getAllowOnePolicy(region, awsAccountId, restApiId, stage, httpMethod, resource));
        }

        return new AuthPolicy(principalId, AuthPolicy.PolicyDocument.getDenyOnePolicy(region, awsAccountId, restApiId, stage, httpMethod, resource));
    }
}
