class HomeController < ApplicationController
  def index
    if params[:tag] != nil
      partition = params[:tag].partition(" | http")

      value = partition[0]
      prop = "http"+partition[2]
      filter = Filter.new(:prop => prop, :value => value)
      redirect_to items_path(:new => filter)
    end
  end
end
