// Mock database of products
const productDatabase = {
  '123456789': {
    name: 'Milk',
    price: 3.99,
    barcode: '123456789'
  },
  '987654321': {
    name: 'Bread',
    price: 2.49,
    barcode: '987654321'
  },
  '456789123': {
    name: 'Eggs',
    price: 4.99,
    barcode: '456789123'
  },
  // Add more products as needed
};

export const getProductDetails = async (barcode) => {
  // Simulate API call delay
  await new Promise(resolve => setTimeout(resolve, 500));
  
  return productDatabase[barcode] || null;
};

export const addProduct = (barcode, product) => {
  productDatabase[barcode] = product;
}; 