#!/bin/bash

# 1. Fetch the server's public key:
openssl s_client -connect auth.tragltech.com:443 -servername auth.tragltech.com </dev/null | openssl x509 -pubkey -noout > pubkey.pem

# 2. Generate the SHA-256 pin:
openssl pkey -pubin -in pubkey.pem -outform DER | openssl dgst -sha256 -binary | openssl base64

# 3. oneliner (step 1 + step 2)
#echo | openssl s_client -connect auth.tragltech.com:443 -servername auth.tragltech.com 2>/dev/null | openssl x509 -pubkey -noout | openssl pkey -pubin -outform DER | openssl dgst -sha256 -binary | openssl base64