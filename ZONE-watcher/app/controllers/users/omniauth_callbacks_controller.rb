class Users::OmniauthCallbacksController < Devise::OmniauthCallbacksController
  def twitter
    auth = request.env["omniauth.auth"]
    @user = User.find_for_twitter_oauth(auth, current_user)
    
    if @user.persisted?
      set_flash_message(:notice, :success, :kind => "Twitter") #if is_navigational_format?
      sign_in_and_redirect @user, :event => :authentication #this will throw if @user is not activated

    else
      session["devise.twitter_data"] = auth
      redirect_to new_user_registration_url
    end
  end
end