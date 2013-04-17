class Users::OmniauthCallbacksController < Devise::OmniauthCallbacksController
  def twitter
    auth = request.env["omniauth.auth"]
    @user = User.find_for_provider_oauth(auth, current_user)
    @user.token = auth.credentials.token
    @user.tokenSecret = auth.credentials.secret


    if @user.persisted?
      set_flash_message(:notice, :success, :kind => "Twitter") #if is_navigational_format?
      sign_in_and_redirect @user, :event => :authentication #this will throw if @user is not activated

    else
      session["devise.twitter_data"] = auth
      redirect_to new_user_registration_url
    end
  end
  def github
    auth = request.env["omniauth.auth"]
    @user = User.find_for_provider_oauth(auth, current_user)
    
    if @user.persisted?
      set_flash_message(:notice, :success, :kind => "Github") #if is_navigational_format?
      sign_in_and_redirect @user, :event => :authentication #this will throw if @user is not activated

    else
      session["devise.github_data"] = auth
      redirect_to new_user_registration_url
    end
  end
  def google_oauth2
    auth = request.env["omniauth.auth"]
    @user = User.find_for_provider_oauth(auth, current_user)
    
    if @user.persisted?
      set_flash_message(:notice, :success, :kind => "Google") #if is_navigational_format?
      sign_in_and_redirect @user, :event => :authentication #this will throw if @user is not activated

    else
      session["devise.google_data"] = auth
      redirect_to new_user_registration_url
    end
  end
end