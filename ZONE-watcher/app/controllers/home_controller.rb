class HomeController < ApplicationController
  def index
    if flash.empty?
      flash[:info] = t 'index.disclaimer'
    end

  end
  def search

  end
end
