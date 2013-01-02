class HomeController < ApplicationController
  def index
    redirect_to "/items"
  end
end
