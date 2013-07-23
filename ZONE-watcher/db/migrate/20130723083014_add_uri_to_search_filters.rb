class AddUriToSearchFilters < ActiveRecord::Migration
  def change
    add_column :search_filters, :uri, :string
  end
end
