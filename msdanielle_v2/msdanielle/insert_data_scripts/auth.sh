echo 'Salvando usuário'
curl -X POST \
    --header "Content-Type: application/json" \
    --data '{"username" : "usuario", "password" : "123456"}' \
    'http://localhost:9094/api/user/save'
echo ''

echo ''
echo '----------------------'
echo ''

echo 'Autenticando usuário'
curl -X GET 'http://localhost:9094/api/authenticate?username=usuario&password=123456'
echo ''



