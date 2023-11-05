# Lambda Authorizer
Tech Challenge Lambda Authorizer

## Pré-requisitos

Antes de executar o projeto, verifique se você possui os seguintes requisitos:
- [Java 17 - Instalar com sdkman](https://sdkman.io/install)
- [Maven - Instalar com sdkman](https://sdkman.io/install) 
- [Terraform](https://developer.hashicorp.com/terraform/tutorials/aws-get-started/install-cli)


## Implantando a aplicação localmente

Para realizar a implantação através do ambiente local será necessário dois passos
- 1 Buildar o projeto
- 2 Realizar o deploy da lambda function com o Terraform

### Builda da aplicação

Após instalação do Java 17 e do maven excute o seguinte comando

```bash
  ./mvnw clean package
```

### Implantando com o Terraform

Tenha o terraform instalado em seu ambiente: [Instalar terraform]

Tenha em mãos as credenciais de acesso da AWS com as permissões para criação de recursos

Exporte as variáveis de ambiente

```bash
export AWS_ACCESS_KEY_ID=<Aws access key>
export AWS_SECRET_ACCESS_KEY=<Aws access secret>
```

Se quiser customizer o deploy cri um arquivo chamado terraform.tfvars redefinindo os valores das variáveis que estão em terraform/variables.tf
Após isso rode os comandos para iniciar o terraform e subir o ambiente

```bash
# Inicia o terraform e instala as dependências
terraform init

# Rodar o terraform plan
terraform plan

# Aplicar a plano para subir a infra
terraform apply -auto-approve

# Destruir a infra: Esse comando remove/deleta todos os recursos criados
terraform destroy -auto-approve
```

