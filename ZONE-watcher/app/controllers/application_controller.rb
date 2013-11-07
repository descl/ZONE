class ApplicationController < ActionController::Base
  protect_from_forgery
  before_filter :set_locale
  def set_locale
    if lang = request.env['HTTP_ACCEPT_LANGUAGE']
      lang = lang[/^[a-z]{2}/]
    end
    I18n.locale = params[:locale] || lang || I18n.default_locale
  end
  # app/controllers/application_controller.rb
  def default_url_options(options={})
    logger.debug "default_url_options is passed options: #{options.inspect}\n"
    { locale: I18n.locale }
  end

  unless Rails.application.config.consider_all_requests_local
    rescue_from Net::HTTP::Persistent::Error, :with => :render_db_error
    rescue_from Mysql2::Error, :with => :render_db_error
  end
  def render_db_error(exception)
    render :template => "/errors/noBD.html", :layout => "empty", :status => 500 and return
  end
end
