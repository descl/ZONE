class SearchFilter < ActiveRecord::Base
  attr_accessible :value, :kind
  belongs_to :search
  def self.build_from_form(value, kind)
    result = SearchFilter.new
    result.value = value
    result.kind = kind
    return result
  end
end
