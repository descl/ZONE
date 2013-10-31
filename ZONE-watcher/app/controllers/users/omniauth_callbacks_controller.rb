class Users::OmniauthCallbacksController < Devise::OmniauthCallbacksController
  def twitter
    auth = request.env["omniauth.auth"]

    #check if we need to associate account to a classic user
    if user_signed_in? && current_user.provider == nil

      if User.where(:provider => auth.provider, :uid => auth.uid).first
        flash[:error] = t('account.twitter.alreadyAssociated')
        redirect_to root_url
      else
        current_user.provider = "twitter+reador"
        current_user.uid = auth.uid
        current_user.token = auth["credentials"].token
        current_user.tokenSecret = auth["credentials"].secret
        current_user.login = auth["info"].nickname
        current_user.save
        current_user.add_timeline_to_sources
        flash[:notice] = t('account.twitter.successAssociated')

        redirect_to root_url
      end
    else
      @user = User.find_for_provider_oauth(auth, current_user)

      if @user.persisted?
        set_flash_message(:notice, :success, :kind => "Twitter") #if is_navigational_format?
        sign_in_and_redirect @user, :event => :authentication #this will throw if @user is not activated

      else
        session["devise.twitter_data"] = auth
        redirect_to new_user_registration_url
      end
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