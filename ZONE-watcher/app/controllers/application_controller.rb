class ApplicationController < ActionController::Base
  protect_from_forgery
  before_filter :set_locale
  def set_locale
    if lang = request.env['HTTP_ACCEPT_LANGUAGE']
      lang = lang[/^[a-z]{2}/]
    end
    I18n.locale = params[:locale] || lang || I18n.default_locale
  end
end
