class SearchSource < ActiveRecord::Base
  attr_accessible :uri
  belongs_to :search
  def self.build_from_form(params)
    result = SearchSource.new
    result.uri = params
    return result
  end
end
