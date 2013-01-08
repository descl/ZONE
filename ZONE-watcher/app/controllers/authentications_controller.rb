class AuthenticationsController < ApplicationController
  def index  
  end  
    
  def create  
    render :text => request.env["rack.auth"].to_yaml  
  end  
    
  def destroy  
  end  
end
