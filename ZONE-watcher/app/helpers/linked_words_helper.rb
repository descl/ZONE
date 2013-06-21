module LinkedWordsHelper
  def escapeText(text)
    return text.gsub("'","\\\\'").gsub("'","\\\\'")
  end
end
