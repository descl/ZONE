class FavoritesController < ApplicationController
  # GET /favorites
  # GET /favorites.json
  def index
    if !user_signed_in?
      flash[:error] = t("devise.failure.unauthenticated")
      redirect_to :back
      return
    end

    sourceURI =  "#{ZoneOntology::ZONE_USER}#{current_user.id}"
    redirect_to url_for(:controller => :searches, :action => :create,  :filters => {:and => [{:uri =>sourceURI, :value=>"favorites"}]}), :method => 'post'Z
  end

  def create
    if !user_signed_in?
      flash[:error] = t("devise.failure.unauthenticated")
      redirect_to :back
      return
    end
    @favorite = Favorite.new(params[:favorite],current_user.id)
    @favorite.save
    render  :layout => 'empty'
  end

  # DELETE /favorites/1
  # DELETE /favorites/1.json
  def destroy
    if !user_signed_in?
      flash[:error] = t("devise.failure.unauthenticated")
      redirect_to :back
      return
    end
    @favorite = Favorite.new(params[:favorite],current_user.id)
    @favorite.destroy

    respond_to do |format|
      format.html { redirect_to favorites_url }
      format.json { head :no_content }
    end
  end
end
