terraform {
  backend "kubernetes" {
    secret_suffix = "tolkien-ai-server-state"
    config_path   = "~/.kube/config"
  }

  required_providers {
    keycloak = {
      source = "mrparkers/keycloak"
      version = "4.1.0"
    }

    onepassword = {
      source = "1Password/onepassword"
      version = "1.1.4"
    }
  }
}

provider "keycloak" {
  client_id = local.terraform_client.client_id
  client_secret = local.terraform_client.client_secret
  url = "https://auth.craigmiller160.us"
}

provider "onepassword" {
  url = "https://infra.craigmiller160.us/onepassword"
  token = var.onepassword_token
}