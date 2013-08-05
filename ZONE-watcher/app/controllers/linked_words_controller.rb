class LinkedWordsController < ApplicationController
  #caches_page :listWords, :autoComplete, :expires_in => 5.minutes

  # GET /linked_words
  # GET /linked_words/$entity.json
  #TODO: imcomprehensible...
  def listWords
    @result = Array.new
    possibleWords = LinkedWord.complete(params[:desc])
    possibleWords.each do |word|
      @result.concat(LinkedWord.getLinkedWords(word[:value]))
    end

    respond_to do |format|
      format.json {
        render json: @result }
    end
  end

  def autoComplete
    cache(params[:desc], :expires_in => 10.minute) do
      @result = LinkedWord.complete(params[:desc])
      respond_to do |format|
        format.json {
          render json: @result }
        end
      end
    end
end
