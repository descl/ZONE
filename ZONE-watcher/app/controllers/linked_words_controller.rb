class LinkedWordsController < ApplicationController
  # GET /linked_words
  # GET /linked_words/$entity.json
  def list
    respond_to do |format|
      format.json {
        @result = []
        @result << params[:desc]
        @result << "first word"
        @result << "second word"
        render json: @result }
    end
  end
end
