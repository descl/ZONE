class LinkedWordsController < ApplicationController
  # GET /linked_words
  # GET /linked_words/$entity.json
  def listWords
    @result = Array.new
    possibleWords = LinkedWord.complete(params[:desc])
    possibleWords.each do |word|
        @result << word
        @result.concat(LinkedWord.getLinkedWords(word))
    end

    respond_to do |format|
      format.json {
        render json: @result }
    end
  end

  def autoComplete
    @result = LinkedWord.complete(params[:desc])

    respond_to do |format|
      format.json {
        render json: @result }
    end
  end
end
