import React, { useState, useEffect } from 'react';
import { Html5QrcodeScanner } from 'html5-qrcode';
import { getProductDetails } from '../utils/database';

const BarcodeScanner = () => {
  const [scannedItems, setScannedItems] = useState([]);
  const [total, setTotal] = useState(0);

  useEffect(() => {
    const scanner = new Html5QrcodeScanner('reader', {
      qrbox: {
        width: 250,
        height: 250,
      },
      fps: 5,
    });

    scanner.render(onScanSuccess, onScanError);

    return () => {
      scanner.clear();
    };
  }, []);

  const onScanSuccess = async (decodedText) => {
    try {
      const product = await getProductDetails(decodedText);
      if (product) {
        setScannedItems(prev => [...prev, product]);
        setTotal(prev => prev + product.price);
      } else {
        alert('Product not found!');
      }
    } catch (error) {
      console.error('Error scanning product:', error);
    }
  };

  const onScanError = (error) => {
    console.warn(error);
  };

  const removeItem = (index) => {
    setScannedItems(prev => {
      const newItems = [...prev];
      setTotal(prevTotal => prevTotal - newItems[index].price);
      newItems.splice(index, 1);
      return newItems;
    });
  };

  return (
    <div className="scanner-container">
      <div id="reader"></div>
      
      <div className="scanned-items">
        <h2>Scanned Items</h2>
        <ul>
          {scannedItems.map((item, index) => (
            <li key={index}>
              {item.name} - ${item.price.toFixed(2)}
              <button onClick={() => removeItem(index)}>Remove</button>
            </li>
          ))}
        </ul>
        
        <div className="total">
          <h3>Total: ${total.toFixed(2)}</h3>
        </div>
      </div>
    </div>
  );
};

export default BarcodeScanner; 