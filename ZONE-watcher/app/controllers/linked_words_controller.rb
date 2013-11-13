class LinkedWordsController < ApplicationController
  #caches_page :listWords, :autoComplete, :expires_in => 5.minutes

  caches_action :autoComplete, :cache_path => Proc.new { |c| c.params }, :expires_in => 1.day
  caches_action :listWords, :cache_path => Proc.new { |c| c.params }, :expires_in => 1.day

  # GET /linked_words
  # GET /linked_words/$entity.json
  #TODO: imcomprehensible...
  def listWords
    @result = Array.new
    possibleWords = LinkedWord.complete(params[:desc])
    possibleWords.each do |word|
      @result.concat(LinkedWord.getLinkedWords(word[:uri]))
    end

    respond_to do |format|
      format.json {
        render json: @result }
    end
  end

  def autoComplete
    puts params[:desc]
    #cache(params[:desc], :expires_in => 10.day) do
      @result = LinkedWord.complete(params[:desc])
    #end

    respond_to do |format|
      format.json {
        render json: @result }
      end
    end
end
