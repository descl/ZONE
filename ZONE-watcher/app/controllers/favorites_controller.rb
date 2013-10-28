class FavoritesController < ApplicationController
  # GET /favorites
  # GET /favorites.json
  def index
    if !user_signed_in?
      flash[:error] = t("devise.failure.unauthenticated")
      begin
        redirect_to(:back)
      rescue ActionController::RedirectBackError
        redirect_to root_path
      end
      return
    end

    sourceURI =  "#{ZoneOntology::ZONE_USER}#{current_user.id}"

    search = nil
    Search.joins(:filters).where(["search_filters.uri = ?", sourceURI]).each do |i|
      puts i.to_json
      if i.filters.length == 1
        search = i
      end
      puts i.filters.to_json
    end

    if search == nil
      redirect_to url_for(:controller => :searches, :action => :create,  :filters => {:and => [{:uri =>sourceURI, :value=>"favorites"}]}, :searchName => t("menu.favoris"), :isNew => false), :method => 'post'
    else
      redirect_to url_for(search)
    end
  end

  def create
    if !user_signed_in?
      flash[:error] = t("devise.failure.unauthenticated")
      begin
        redirect_to(:back)
      rescue ActionController::RedirectBackError
        redirect_to root_path
      end
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
      begin
        redirect_to(:back)
      rescue ActionController::RedirectBackError
        redirect_to root_path
      end
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
