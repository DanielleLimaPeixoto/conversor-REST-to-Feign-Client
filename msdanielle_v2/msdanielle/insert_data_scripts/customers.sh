echo 'Inserindo consumidor 1'
curl -X GET 'http://localhost:9093/customer/add/customer01/000.000.000-01/customer01@email.com'
echo ''

echo 'Inserindo consumidor 2'
curl -X GET 'http://localhost:9093/customer/add/customer02/000.000.000-02/customer02@email.com'
echo ''

echo 'Inserindo consumidor 3'
curl -X GET 'http://localhost:9093/customer/add/customer03/000.000.000-03/customer03@email.com'
echo ''

echo 'Lista de consumidores'
curl -X GET 'http://localhost:9093/customer/getCustomers'
echo ''