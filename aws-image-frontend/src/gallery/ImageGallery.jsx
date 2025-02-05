import React, { useState } from 'react';
import axios from 'axios';

const ImageGallery = () => {
    const [filterWord, setFilterWord] = useState('');
    const [images, setImages] = useState([]);

    const handleFilterChange = (e) => {
        setFilterWord(e.target.value);
    };

    const fetchImages = async () => {
        try {
            const response = await axios.get('http://localhost:8080/api/v1/get-by-filter', {
                params: { filterWord }
            });

            setImages(response.data);
        } catch (error) {
            console.error('Error fetching images:', error);
        }
    };

    const handleSearchClick = () => {
        if (filterWord) {
            fetchImages();
        } else {
            alert('Please enter a filter word');
        }
    };

    return (
        <div>
            <label>Please find me images that contain the following items:</label>
            <input
                type="text"
                value={filterWord}
                onChange={handleFilterChange}
                placeholder="Enter filter word"
            />
            <button onClick={handleSearchClick}>Search</button>

            <div className="image-gallery">
                {images.length > 0 ? (
                    images.map((image, index) => (
                        <div key={index}>
                            <img src={`${image}`} alt={`Filtered object ${index}`} />
                        </div>
                    ))
                ) : (
                    <p>No images found</p>
                )}
            </div>
        </div>
    );
};

export default ImageGallery;
