package br.com.fiap.techchallenge.lambdaauthorizer;

import br.com.fiap.techchallenge.lambdaauthorizer.io.AuthPolicy;
import br.com.fiap.techchallenge.lambdaauthorizer.io.TokenAuthorizerContext;
import br.com.fiap.techchallenge.lambdaauthorizer.models.UserRole;
import br.com.fiap.techchallenge.lambdaauthorizer.services.AuthClient;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.Arrays;

public class APIGatewayAuthorizerHandler implements RequestHandler<TokenAuthorizerContext, AuthPolicy> {
    @Override
    public AuthPolicy handleRequest(TokenAuthorizerContext input, Context context) {

        var logger = context.getLogger();

        String token = input.getAuthorizationToken();

        logger.log("Token: " + token + "\n");
        logger.log("Method ARN: " + input.getMethodArn() + "\n");

        String methodArn = input.getMethodArn();
        String[] arnPartials = methodArn.split(":");
        String region = arnPartials[3];
        String awsAccountId = arnPartials[4];
        String[] apiGatewayArnPartials = arnPartials[5].split("/");
        String restApiId = apiGatewayArnPartials[0];
        String stage = apiGatewayArnPartials[1];
        AuthPolicy.HttpMethod httpMethod = AuthPolicy.HttpMethod.valueOf(apiGatewayArnPartials[2]);

        String resource = ""; // root resource
        if (apiGatewayArnPartials.length >= 4) {
            resource = Arrays.stream(apiGatewayArnPartials).skip(3).reduce((a, b) -> a + "/" + b).get();
        }

        var authClient = new AuthClient(logger);
        var user = authClient.getUserInfo(token);

        String principalId = user.getId();

        if (user.getRoles().contains(UserRole.GUEST) || user.getRoles().contains(UserRole.CUSTOMER)) {
            logger.log("region: " + region + ", awsAccountId: " + awsAccountId + ", restApiId: " + restApiId + ", stage: " + stage + ", httpMethod: " + httpMethod + ", resource: " + resource + "\n");
            return new AuthPolicy(principalId, AuthPolicy.PolicyDocument.getAllowOnePolicy(region, awsAccountId, restApiId, stage, httpMethod, resource));
        }
        logger.log("Policy denied");
        return new AuthPolicy(principalId, AuthPolicy.PolicyDocument.getDenyOnePolicy(region, awsAccountId, restApiId, stage, httpMethod, resource));
    }
}
