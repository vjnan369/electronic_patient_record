alter table oauth_client_token
change `username` `user_name` varchar(256);

alter table oauth_access_token
change `username` `user_name` varchar(256);

