class Search < ActiveRecord::Base
  attr_accessible :name
  belongs_to :user
  has_many :sources, class_name: "SearchSource"
  has_many :filters, class_name: "SearchFilter"

  def self.build_from_form(params)
    result = Search.new
    params[:arraySource].split(",").each do |source|
      result.sources << SearchSource.build_from_form(source)
    end
    params[:arrayFiltering].split(",").each do |filter|
      result.filters << SearchFilter.build_from_form( filter,"with")
    end
    return result
  end
end
