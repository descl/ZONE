config_file = File.join(Rails.root,'config','config.yml')
raise "#{config_file} is missing!  please create it using #{config_file}.example" unless File.exists? config_file
loaded_config = YAML.load_file(config_file)

#mailchimp configuration
Devise.mailchimp_api_key = loaded_config["devise"]["mailchimp"]["apiKey"]
Devise.mailing_list_name = loaded_config["devise"]["mailchimp"]["listName"]
Devise.double_opt_in = false
Devise.send_welcome_email = false