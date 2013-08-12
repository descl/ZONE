class FavoritesController < ApplicationController
  # GET /favorites
  # GET /favorites.json
  def index
    gonItemsUri = Array.new
    favs  =Favorite.all(current_user.id)
    @sparqlRequest = favs[:query]
    @favorites = favs[:result]
    @favorites.each do |item|
      gonItemsUri << item_path(:id => item.uri)
    end
    gon.gonItemsUri = gonItemsUri

    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @favorites }
    end
  end

  def create
    @favorite = Favorite.new(params[:favorite],current_user.id)
    @favorite.save
    render  :layout => 'empty'
  end

  # DELETE /favorites/1
  # DELETE /favorites/1.json
  def destroy
    @favorite = Favorite.new(params[:favorite],current_user.id)
    @favorite.destroy

    respond_to do |format|
      format.html { redirect_to favorites_url }
      format.json { head :no_content }
    end
  end
end
