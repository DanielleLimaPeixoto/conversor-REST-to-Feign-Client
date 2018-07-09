echo 'Inscrevendo usuário'
curl -X POST \
    --header "Content-Type: application/json" \
    --data '{"cpf" : "000.000.000-00", "email" : "meuemail@meuprovedor.com"}' \
    'http://localhost:9095/api/newsletter/subscribe'
echo ''

echo ''
echo '----------------------'
echo ''

echo 'Publicando newsletter para inscritos'
curl -X GET 'http://localhost:9095/api/newsletter/publish'
echo ''