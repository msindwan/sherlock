/**
 * Sherlock Search Viewer component
 *
 * @Author : Mayank Sindwani
 * @Date   : 2017-09-22
 *
 * Description :
 **/

 import React from 'react';

 const SearchViewer = ({ searchResults }) => (
     <div>
     {
         searchResults.map((result, i) => {
             return (
                 <div key={i}
                     className="file-browser-cell">
                         {result.path}
                         <div>
                             {
                                 result.frags.map((frag, j) => {
                                     return (
                                         <div key={j} dangerouslySetInnerHTML={{ __html: frag }}></div>
                                     )
                                 })
                             }
                         </div>
                 </div>
             );
         })
     }
     </div>
 );

 export default SearchViewer;
