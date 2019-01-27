# playground-spring-cloud-config-server

Config Server use in tests that is used  File System Backend

Start using ConfigServerApplication
   Server listens on port 7777 defined in bootstrap.yml
  
properties shared from  classpath:/shared-configs

  Example:
     encrypt-override.yml   see http://localhost:7777/encrypt-override/p1 and http://localhost:7777/encrypt-override/p2
     